Karma-Decision-Tree-Service
===========================

A RESTful service based on Decision Tree implementation 'rpart' in R.

**To set up the service**

    * Compile and create WAR file:

        + Open Terminal and go to dt-service folder
        + Run *mvn clean package*

    * Copy the WAR file from *dt-service/target/* to *webapps* folder of Tomcat

    * Start the Tomcat server

**To run the service**
    
    * To train and create a Decision Tree model:

        + Send a POST request with payload as contents of the training data in CSV format to

            *http://<host-name>:<port>/dt-service/api/dt/train*

        + By default, the last column is assumed to contain the class labels
        + To specify column-number of class labels, add *classColumnNumber* parameter:

            *http://<host-name>:<port>/dt-service/api/dt/train?classColumnNumber=<column-number>*

        + The response will contain a summary of the training phase and the name of the created model at its bottom
        + Note down the model name (which starts with 'DT' and ends with 'model')

    * To test a model:

        + Send a POST request with payload as contents of the testing data in CSV format to

            *http://<host-name>:<port>/dt-service/api/dt/test?modelName=<model-name>*

        + To specify column-number of class labels, add *classColumnNumber* parameter:

            *http://<host-name>:<port>/dt-service/api/dt/test?modelName=<model-name>&classColumnNumber=<column-number>*

        + By default, the output of the testing phase is a confusion matrix
        + To specify the output type of the testing phase, add *outputType* parameter:

            - *http://<host-name>:<port>/dt-service/api/dt/test?modelName=<model-name>&outputType=confusion_matrix*
            - *http://<host-name>:<port>/dt-service/api/dt/test?modelName=<model-name>&outputType=predictions*