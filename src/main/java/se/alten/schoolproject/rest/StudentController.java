package se.alten.schoolproject.rest;

import lombok.NoArgsConstructor;
import se.alten.schoolproject.dao.SchoolAccessLocal;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.model.StudentModel;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Stateless
@NoArgsConstructor
@Path("/student")
public class StudentController {

    private static final Logger LOGGER = (Logger) Logger.getLogger(StudentController.class.getName());

    @Inject
    private SchoolAccessLocal sal;

    //return all students
    @GET
    @Produces({"application/JSON"})
    public Response showStudents() {
        try {
            List students = sal.listAllStudents();
            return Response.ok(students).build();
        } catch ( Exception e ) {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }


    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({"application/JSON"})
    /**
     * JavaDoc
     */
    public Response addStudent(String studentModel) {
        try {

            StudentModel answer = sal.addStudent(studentModel);
            LOGGER.info(answer.toString());
            System.out.println("answer: " + answer);
            switch ( answer.getForename()) {
                case "empty":
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{\"Fill in all details please\"}").build();
                case "duplicate":
                    return Response.status(Response.Status.EXPECTATION_FAILED).entity("{\"Email already registered!\"}").build();
                default:
                    System.out.println("---new student was added");
                    return Response.ok(answer).build();
            }
        } catch ( Exception e ) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("/delete/{email}")
    public Response deleteUser( @PathParam("email") String email) {
        try {
            sal.removeStudent(email);
            return Response.ok().build();
        } catch ( Exception e ) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    //toDo  change to StudentModel
    @PUT
    @Path("/update")
    public void updateStudent( @QueryParam("forename") String forename, @QueryParam("lastname") String lastname, @QueryParam("email") String email) {
        sal.updateStudent(forename, lastname, email);
    }

    @PATCH
    public void updatePartialAStudent(String studentModel) {
        sal.updateStudentPartial(studentModel);
    }

    @GET
    @Path("find/{name}")
    @Produces({"application/JSON"})
    public Response findStudentByName(@PathParam("name") String name) {
        try {
            StudentModel student = sal.findStudentByName(name);
            return Response.ok(student).build();
        } catch ( Exception e ) {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
}



