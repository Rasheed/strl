package com.strl.services;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/mood")
@Produces(MediaType.APPLICATION_JSON)
public class MoodService {	

	@POST 
	@Path("/form")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void save(@FormParam("a") String active,
		      @FormParam("e") String edgy,
		      @FormParam("en") String energetic,
		      @FormParam("ha") String happy,
		      @FormParam("ne") String nervous,
		      @FormParam("ca") String calm,
		      @FormParam("co") String contented,
		      @FormParam("sl") String sluggish,
		      @FormParam("sa") String sad,
		      @FormParam("re") String relaxed,
		      @FormParam("reg") String regretful,
		      @FormParam("pa") String passive,
		      @Context HttpServletResponse servletResponse){	
		
	}
	
	
}
