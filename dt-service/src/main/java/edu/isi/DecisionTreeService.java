package edu.isi;

import java.io.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/dt")
public class DecisionTreeService {
    
    private final String newline = System.getProperty("line.separator");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy-HmsS");
    private String resourcesPath = (new File((getClass().getResource("/")).getFile())).getAbsolutePath() + "/R_Scripts";

    
    /** Saves URL Encoded type String as a CSV file
     * 
     * @param data URL Encoded data
     * @param filepath Path of the file where the data has to be saved as CSV
     * 
     */
    private void saveCSV(String data, String filePath) throws IOException {

        FileWriter writer = new FileWriter(filePath);
        String[] rows = data.split(newline);
        for(String rowString : rows) {

                String[] row = rowString.split("\t");
                for(int i = 0; i < row.length; i++)
                {
                    writer.append(row[i]);
                    if(i != row.length - 1)
                    writer.append(",");
                }
                writer.append(newline);
            }
            writer.close();
    
    }

    /** Executes the given command and returns the response as a String
     * 
     * @param command The command to be executed.
     * 
     * @return Response of the execution of the command.
     */
    private String executeCommand(String command) throws Exception {

        Process pr = Runtime.getRuntime().exec(command);
        BufferedReader prInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = null;
        String response = "";
        while((line = prInput.readLine()) != null) {
        
               response += line;
               response += newline;
        
        }
        return response;

    }

    /** Deletes a file
     * 
     * @param filePath Path of the file to be deleted.
     * 
     */
    private void delete(String filePath) throws Exception {

        File f = new File(filePath);
        f.delete();

    }

    /** Returns path of R model given it's name. The model can be saved/accessed at this path.
     * Also makes sure that the 'models' directory exists
     * 
     * @param modelName Unique name of the model
     * 
     * @return Path at which the model can be saved/accessed.
     */
    private String getModelPath(String modelName) {

        String modelDirectoryPath = resourcesPath + "/models";
        File dir = new File(modelDirectoryPath);
        dir.mkdir();
        return modelDirectoryPath + "/" + modelName + ".RData";        

    }

    /** Method processing HTTP GET requests, producing "text/plain" MIME media
     * type.
     * 
     * @return String that will be send back as a response of type "text/plain".
     */
    @GET 
    @Produces("text/plain")
    public String getIt(){
        return "Works!";
    }

    /** Method handling request for training a Decision Tree model with training data
     * sent as POST, produces "text/plain" summary of the trained model.
     * 
     * @param data Training data
     * 
     * @return String that contains details of trained model and unique model-name.
     */
    @POST
    @Path("/train")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response train(String data) throws Exception {

        String dataPath = resourcesPath + "/Train_" + sdf.format(Calendar.getInstance().getTime()) + ".csv";
        saveCSV(data, dataPath);


        String modelName = "DT-" + sdf.format(Calendar.getInstance().getTime()) + "-model";
        String modelPath = getModelPath(modelName);
        
        String command = "Rscript " + resourcesPath + "/DTTraining.R " + dataPath + " " + modelPath;
        String response = executeCommand(command);
        response += newline + modelName + " created!" + newline;
        
        delete(dataPath);

        return Response.status(200).entity(response).build();
    }

    /** Method handling request for testing a Decision Tree model with testing data
     * sent as POST, produces "text/plain" confusion matrix or predicted labels, depending on
     * specified output-type
     * 
     * @param data Testing data
     * @param outputType takes value: "predictions" or "confusion_matrix" to decide output-type for testing
     * 
     * @return Output of the testing phase in the outputType format
     */
    @POST
    @Path("/test")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response test(String data, @QueryParam("modelName") String modelName,
                        @QueryParam("outputType") String outputType) throws Exception {

        String dataPath = resourcesPath + "/Test_" + sdf.format(Calendar.getInstance().getTime()) + ".csv";
        saveCSV(data, dataPath);

        String modelPath = getModelPath(modelName);
        
        String command = "Rscript " + resourcesPath + "/DTTesting.R " + dataPath + " " + modelPath + " " + outputType;
        String response = executeCommand(command);

        delete(dataPath);

        return Response.status(200).entity(response).build();
    }

}
