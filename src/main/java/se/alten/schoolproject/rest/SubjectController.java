package se.alten.schoolproject.rest;

import lombok.NoArgsConstructor;
import se.alten.schoolproject.dao.SchoolAccessLocal;
import se.alten.schoolproject.model.SubjectModel;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Stateless
@NoArgsConstructor
@Path("/subject")
public class SubjectController {

  private static final Logger LOGGER = (Logger) Logger.getLogger(SubjectController.class.getName());

  @Inject
  private SchoolAccessLocal sal;


  @GET
  @Produces({"application/JSON"})
  public Response getSubjects() {
    LOGGER.info("Controller: getSubjects()");
    try {
      List<SubjectModel> result = sal.getSubjects();
      return Response.ok(result).build();
    } catch (Exception e) {
      return Response.status(Response.Status.CONFLICT).build();
    }
  }


  @POST
  @Path("/add")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces({"application/JSON"})
  public Response addSubject(String subjectBody) {
    LOGGER.info("Controller: addSubject()");
    try {
      SubjectModel result = sal.addSubject(subjectBody);
      if (result.getTitle().equals("empty")) {
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{\"Fill in all details please\"}").build(); //406
      }
      return Response.ok(result).build();
    } catch (EJBTransactionRolledbackException | PersistenceException e) {
      LOGGER.info("addSubject: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"Subject already exist!\"}").build(); //417
    } catch (RuntimeException e) {
      LOGGER.info("addSubject: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.NOT_FOUND).entity("{\"Could not find resource for full path!\"}").build(); //404
    } catch (Exception e) {
      LOGGER.info("addSubject: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.BAD_REQUEST).entity("{\"Oops. Server side error!\"}").build(); //400
    }
  }


  @DELETE
  @Produces({"application/JSON"})
  @Path("/delete/{title}")
  public Response deleteSubject(@PathParam("title") String title) {
    LOGGER.info("Controller: deleteSubject()");
    try {
      String result = sal.deleteSubject(title);
      if (result.equals("empty")) {
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{\"Can't delete subject with empty title!\"}").build(); //406
      }
      if (result.equals("")) {
        return Response.status(Response.Status.NOT_FOUND).entity("{\"Subject not found!\"}").build(); //406
      }
      return Response.ok().entity("{\"Subject \"" + result + "\" was deleted from database!\"}").build();
    } catch (NoResultException e) {
      LOGGER.info("deleteSubject: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"Subject with current title not found!\"}").build(); //417
    } catch (EJBTransactionRolledbackException | PersistenceException e) {
      LOGGER.info("deleteSubject: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"Subject not found!\"}").build(); //417
    } catch (RuntimeException e) {
      LOGGER.info("deleteSubject: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.NOT_FOUND).entity("{\"Could not find resource for full path!\"}").build(); //404
    } catch (Exception e) {
      LOGGER.info("deleteSubject: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.BAD_REQUEST).entity("{\"Oops. Server side error!\"}").build(); //400
    }
  }


  @PUT
  @Produces({"application/JSON"})
  @Path("/update")
  public Response updateSubject(@QueryParam("title") String title, @QueryParam("newTitle") String newTitle) {
    try {
      LOGGER.info("---updateSubject---");
      SubjectModel subjectModel = sal.updateSubject(title,newTitle);
      return Response.ok(subjectModel).build();
    } catch (EJBTransactionRolledbackException | PersistenceException e) {
      LOGGER.info("updateSubject: " + e.toString());
      return Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"Subject not found!\"}").build(); //417
    } catch (RuntimeException e) {
      LOGGER.info("updateSubject: " + e.toString());
      return Response.status(Response.Status.NOT_FOUND).entity("{\"Could not find resource for full path!\"}").build(); //404
    } catch (Exception e) {
      LOGGER.info("updateSubject: " + e.toString());
      return Response.status(Response.Status.BAD_REQUEST).entity("{\"Oops. Server side error!\"}").build(); //400
    }
  }


  @GET
  @Path("find/{title}")
  @Produces({"application/JSON"})
  public Response findSubjectByTitle(@PathParam("title") String title) {
    try {
      LOGGER.info("Controller: findSubjectByTitle()");
      SubjectModel result = sal.findSubjectByTitle(title);
      return Response.ok(result).build();
    } catch (EJBTransactionRolledbackException | PersistenceException e) {
      LOGGER.info("findSubjectByTitle: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"Subject with current title not found!\"}").build(); //417
    } catch (RuntimeException e) {
      LOGGER.info("findSubjectByTitle: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.NOT_FOUND).entity("{\"Could not find resource for full path!\"}").build(); //404
    } catch (Exception e) {
      LOGGER.info("findSubjectByTitle: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.BAD_REQUEST).entity("{\"Oops. Server side error!\"}").build(); //400
    }
  }


  @POST
  @Path("/add/student/{subjectTitle}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces({"application/JSON"})
  public Response addStudentToSubject(@PathParam("subjectTitle") String subjectTitle, String studentsBody) {
    LOGGER.info("Controller: addStudentToSubject()");
    try {
      String result = sal.addStudentToSubject(subjectTitle, studentsBody);
      if (result == null) {
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{\"Fill in all details please\"}").build(); //406
      }
      return Response.ok(result).build();
    } catch (EJBTransactionRolledbackException | PersistenceException e) {
      LOGGER.info("addStudentToSubject: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"Student already exist!\"}").build(); //417
    } catch (RuntimeException e) {
      LOGGER.info("addStudentToSubject: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.NOT_FOUND).entity("{\"Could not find resource for full path!\"}").build(); //404
    } catch (Exception e) {
      LOGGER.info("addStudentToSubject: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.BAD_REQUEST).entity("{\"Oops. Server side error!\"}").build(); //400
    }
  }



  @POST
  @Path("/add/teacher/{subjectTitle}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces({"application/JSON"})
  public Response addTeacherToSubject(@PathParam("subjectTitle") String subjectTitle, String teacherBody) {
    LOGGER.info("Controller: addTeacherToSubject()");
    try {
      String result = sal.addTeacherToSubject(subjectTitle, teacherBody);
      if (result == null) {
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{\"Fill in all details please\"}").build(); //406
      }
      return Response.ok(result).build();
    } catch (EJBTransactionRolledbackException | PersistenceException e) {
      LOGGER.info("addTeacherToSubject: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"Teacher already exist!\"}").build(); //417
    } catch (RuntimeException e) {
      LOGGER.info("addTeacherToSubject: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.NOT_FOUND).entity("{\"Could not find resource for full path!\"}").build(); //404
    } catch (Exception e) {
      LOGGER.info("addTeacherToSubject: " + e.getClass().getSimpleName());
      return Response.status(Response.Status.BAD_REQUEST).entity("{\"Oops. Server side error!\"}").build(); //400
    }
  }


}
