package employees;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.servlet.ServletContext;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/")
public class EmployeesRS {
    @Context
    private ServletContext sctx;  // dependency injection
    private static EmployeesList elist;  // set in populate()


    /*  No-Arg Constructor  */
    public EmployeesRS() { }


    /* REST methods */


    // XML GET
    @GET
    @Path("/xml")
    @Produces({MediaType.APPLICATION_XML})
    public Response getXml() {
        checkContext();
        return Response.ok(elist, "application/xml").build();
    }

    @GET
    @Path("/xml/{id: \\d+}")
    @Produces({MediaType.APPLICATION_XML})  // could use "application/xml" instead
    public Response getXml(@PathParam("id") int id) {
        checkContext();
        return toRequestedType(id, "application/xml");
    }

    // JSON GET
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json")
    public Response getJson() {
        checkContext();
        return Response.ok(toJson(elist), "application/json").build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json/{id: \\d+}")
    public Response getJson(@PathParam("id") int id) {
        checkContext();
        return toRequestedType(id, "application/json");
    }

    // PLAIN TEXT GET
    @GET
    @Path("/plain")
    @Produces({MediaType.TEXT_PLAIN})
    public String getPlain() {
        checkContext();
        return elist.toString();
    }


    // PLAIN TEXT POST
    @POST
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/create")
    public Response create(@FormParam("name") String name,
                           @FormParam("socialSecurityNumber") String socialSecurityNumber) {

        checkContext();
        String msg = null;

        // Require both properties to create to enfore Employee constuctor.
        if (name == null || socialSecurityNumber == null) {
            msg = "Property 'name' or 'socialSecurityNumber' is missing.\n";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }

        // Otherwise, create the Employee and add it to the collection.
        int id = addEmployee(name, socialSecurityNumber);
        msg = "Employee " + id + " created: (name = " + name + " socialSecurityNumber = " + socialSecurityNumber + ").\n";

        return Response.ok(msg, "text/plain").build();
    }


    // PLAIN TEXT PUT
    @PUT
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/update")
    public Response update(@FormParam("id") int id,
                           @FormParam("name") String name,
                           @FormParam("socialSecurityNumber") String socialSecurityNumber) {


        checkContext();

        String msg = null;

        // Check that sufficient data are present to do an edit.
        if (name == null && socialSecurityNumber == null) {
            msg = "Neither name nor socialSecurityNumber is given: nothing to edit.\n";
        }


        Employee e = elist.find(id);

        // Check that employee id exists
        if (e == null) {
            msg = "There is no employee with ID " + id + "\n";
        }

        if (msg != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }

        // Update

        if (name != null) {
            e.setName(name);
        }

        if (socialSecurityNumber != null) {
            e.setSocialSecurityNumber(socialSecurityNumber);
        }

        msg = "Employee " + id + " has been updated.\n";
        return Response.ok(msg, "text/plain").build();
    }


    // PLAIN TEXT DELETE
    @DELETE
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/delete/{id: \\d+}")
    public Response delete(@PathParam("id") int id) {
        checkContext();
        String msg = null;

        Employee e = elist.find(id);

        if (e == null) {
            msg = "there is no employee with ID " + id + " . Cannot delete.\n";

            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        elist.getEmployees().remove(e);
        msg = "Employee " + id + " deleted.\n";

        return Response.ok(msg, "text/plain").build();
    }



    /*  Utilities  */

    private void checkContext() {
        if (elist == null) {
            populate();
        }
    }


    private void populate() {
        elist = new EmployeesList();

        String filename = "/WEB-INF/data/employees.db";
        InputStream in = sctx.getResourceAsStream(filename);

        // Read the data into the array of Employees.
        if (in != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                int i = 0;
                String record = null;

                while ((record = reader.readLine()) != null) {
                    String[] parts = record.split("!");
                    addEmployee(parts[0], parts[1]);
                }
            }
            catch(Exception e) {
                throw new RuntimeException("I/O failed!");
            }
        }
    }


    // Add a new employee to the list.
    private int addEmployee(String name, String socialSecurityNumber) {
        int id = elist.add(name, socialSecurityNumber);
        return id;
    }


    // Employee  --> JSON document
    private String toJson(Employee employee) {
        String json = "If you see this, there's a problem.";
        try {
            json = new ObjectMapper().writeValueAsString(employee);
        }
        catch(Exception e) { }

        return json;
    }

    // EmployeesList  --> JSON document
    private String toJson(EmployeesList elist) {
        String json = "if you see this, there's a problem.";
        try {
            json = new ObjectMapper().writeValueAsString(elist);
        }
        catch(Exception e ) {  }

        return json;
    }


    // Generate an HTTP error response or typed OK response
    private Response toRequestedType(int id, String type) {
        Employee empl = elist.find(id);
        if (empl == null) {
            String msg = id + " is a bad ID.\n";

            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        else if (type.contains("json")) {
            return Response.ok(toJson(empl), type).build();
        }
        else {
            return Response.ok(empl, type).build(); // toXml is automatic
        }
    }


}








