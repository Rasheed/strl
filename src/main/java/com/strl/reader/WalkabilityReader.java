package com.strl.reader;

import gnu.trove.list.TLongList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.CmdArgs;

public class WalkabilityReader {

	Map<TLongList, Long> map = new HashMap<TLongList, Long>();
	private List<TLongList> nodeList;
	Map<Long, Double> idToWalkabilityMap = new HashMap<Long, Double>();

	public static void main(String argv[]) {
		GraphHopper hopper = new GraphHopper().forServer()
				.setOSMFile("src/main/resources/centrallondon.xml")
				.setEncodingManager(new EncodingManager(EncodingManager.FOOT))
				.setInMemory().init(new CmdArgs());
		hopper.importOrLoad();

	}

	public void setMap(Map<TLongList, Long> map) {
		this.map = map;
		List<TLongList> list = new ArrayList<TLongList>(map.keySet());
		this.nodeList = list;
	}

	public Long findWayId(Long start, Long end) {
		Long id = null;
		for (TLongList list : nodeList) {
			if (list.contains(start) && list.contains(end)) {
				// System.out.println(start +" " + end);
				id = map.get(list);
				// System.out.println(id);
			}
		}
		return id;

	}

	public void addWalkabilityToXML() {
		try {
			String filepath = "/Users/rasheedwihaib/prelimstrlapp/src/main/resources/centrallondon.xml";
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);

			// Get the root element
			Node osm = doc.getFirstChild();

			// Get the staff element , it may not working if tag has spaces, or
			// whatever weird characters in front...it's better to use
			// getElementsByTagName() to get it directly.
			// Node staff = company.getFirstChild();

			// Get the staff element by tag name directly
			int j = 0;
			int size =  idToWalkabilityMap.size();
			
			System.out.println(size);
			NodeList ways = doc.getElementsByTagName("way");
			for (int i = 0; i < ways.getLength(); i++) {
				if(j > size) {
					return;
				}
				Node way = ways.item(i);
				NamedNodeMap attr = way.getAttributes();
				Node nodeAttr = attr.getNamedItem("id");
				Long id = Long.valueOf(nodeAttr.getTextContent());
				// append a new node to staff
				Element walkability = doc.createElement("tag");
				Double walkabilityscore = idToWalkabilityMap.get(id);
				if(walkabilityscore == null) {
					walkabilityscore = 0D;
				}
				walkability.setAttribute("k", "walkability");
				walkability.setAttribute("v", walkabilityscore+"");
				way.appendChild(walkability);
				if(walkabilityscore > 0) {
					System.out.println("added walkability= "+walkabilityscore+" for way "+id);
					j++;
					
					System.out.println((j*100/size) + "% completed");
				}
			}

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filepath));
			transformer.transform(source, result);

			System.out.println("Done");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}

	}

	public void setWalkabilitiesInXML() throws IOException {
		BufferedReader TSVFile = new BufferedReader(
				new FileReader(
						"/Users/rasheedwihaib/prelimstrlapp/src/main/resources/london_walkscore4graphhopper.tsv"));

		String dataRow = TSVFile.readLine();
		List<WalkabilityTSVValue> list = new ArrayList<WalkabilityReader.WalkabilityTSVValue>();
		while (dataRow != null) {
			String[] dataArray = dataRow.split("\t");
			Long startNode = Long.valueOf(dataArray[0]);
			Long endNode = Long.valueOf(dataArray[1]);
			Double walkability = Double.valueOf(dataArray[2]);
			WalkabilityTSVValue tsvValue = new WalkabilityTSVValue(startNode,
					endNode, walkability);
			list.add(tsvValue);
			dataRow = TSVFile.readLine(); // Read next line of data.
		}

		TSVFile.close();

		for (WalkabilityTSVValue row : list) {
			Long id = findWayId(row.start, row.end);
			if (id != null) {
				idToWalkabilityMap.put(id, row.walkability);
			}
		}
		addWalkabilityToXML();
	}

	public class WalkabilityTSVValue {
		long start;
		long end;
		double walkability;

		public WalkabilityTSVValue(long start, long end, double walkability) {
			this.start = start;
			this.end = end;
			this.walkability = walkability;
		}

	}
}
