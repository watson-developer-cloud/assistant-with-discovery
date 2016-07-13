# Conversation Enhanced Sample Application

[![Build Status](https://travis-ci.org/watson-developer-cloud/conversation-enhanced.svg?branch=master)](http://travis-ci.org/watson-developer-cloud/conversation-enhanced)

This application demonstrates the combination of the Conversation and Retrieve and Rank services. First, users pose questions to the Conversation service. If Conversation is not able to confidently answer, Conversation Enhanced executes a call to Retrieve and Rank to provide the user with a list of helpful answers.

<b>Either way you deploy this app, you must have a Bluemix account and run some steps within Bluemix.</b>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<img src="readme_images/bluemix.png" width="200"/>](#bluemix)     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<img src="readme_images/local.png" width="200"/>](#local)

# How the app works
The application is designed and trained for chatting with a cognitive car.  The chat interface is on the left, and the JSON that the JavaScript code receives from the server is on the right. A user is able to ask two primary categories of questions.

Commands may be issued to the car to perform simple operations.  These commands are run against a small set of sample data trained with intents like "turn_on", "weather", and "capabilities"

Example commands that can be executed by the Conversation service are: 

    turn on windshield wipers
    play music

This app has also ingested and trained itself based on a car manual. In addition to conversational commands, you can also ask questions that you would expect to have answered in your car manual. For example:

    How do I check my tire pressure
    How do I turn on cruise control



To watch a video about the code behind this app, see below.

[<img src="readme_images/video.PNG" />](https://www.youtube.com/watch?v=wG2fuliRVNk)

<a name="bluemix">
# Getting Started using Bluemix
</a>

![](readme_images/Deploy on Bluemix - EIR app.png)

## Before you begin
1 Ensure that you have a [Bluemix account](https://console.ng.bluemix.net/registration/).

2 Ensure that you have the necessary space available in your Bluemix account. This action deploys 1 application and 3 services.
   * You can view this on your Bluemix Dashboard. Tiles will show what space you have available.
   * For example, for Services & APIS

    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![](readme_images/services.PNG)

## Deploy the App
1 Select Deploy to Bluemix

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; [![Deploy to Bluemix](https://bluemix.net/deploy/button.png)](https://bluemix.net/deploy?repository=https://github.com/watson-developer-cloud/conversation-enhanced)

2 Log in with an existing Bluemix account or sign up.

3 Name your app and select your REGION, ORGANIZATION, and SPACE. Then select DEPLOY.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ![](readme_images/deployapp1.PNG)

* This performs multiple actions:
  - Creates the app
  - Creates a Document Conversion service instance for use with the Retrieve & Rank tooling
  - Creates a Conversation service instance that the user needs for workspace creation
  - Creates a Retrieve & Rank service instance

* The status of the deployment is shown. This can take some time.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![](readme_images/createproject.PNG)

4 Once your app has deployed, select VIEW YOUR APP.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![](readme_images/viewyourapp.PNG)

5 This lauches more actions, including:
  - Creating a SOLR cluster, config, and collection in the Retrieve & Rank service
  - Ingesting documents into the collection
  - Creating a trained ranker to aide in answering questions

A dialog shows the progress.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![](readme_images/deployPicture.PNG)

<a name="returnbluemix">
7 When setup is complete, you need to add a WORKSPACE_ID . Navigate to your Bluemix Dashboard and [import a workspace](#workspace). Setup your workspace then return to these steps.
</a>

8 After you have set up a workspace, [add the WORKSPACE_ID environment variable](#env).

<a name="local">
# Getting Started locally
</a>

![](readme_images/Deploy locally - EIR app.png)


## Before you begin

- Ensure that you have [Websphere Liberty Profile Server](https://developer.ibm.com/wasdev/downloads/liberty-profile-using-non-eclipse-environments/).


## Building locally

To build the application:

1 Git clone the project `https://github.com/watson-developer-cloud/conversation-enhanced`

2 Navigate to the `conversation-enhanced` folder

3 For Windows, type `gradlew.bat build`. Otherwise, type `gradlew build`.
- If you prefer, use your locally installed Gradle plugin instead of this provided wrapper.

4 The built WAR file (conversation-enhanced-0.1-SNAPSHOT.war) is in the `conversation-enhanced/build/libs/` folder.

5 Copy the WAR file into the Liberty install directory's dropins folder. For example, `<liberty install directory>/usr/servers/<server profile>/dropins`.

## Setup Bluemix components

1 Ensure that you have a [Bluemix account](https://console.ng.bluemix.net/registration/). While you can do part of this deployment locally, you must still use Bluemix.

<a name="returnlocal">
2 In Bluemix, [create a Conversation Service](http://www.ibm.com/watson/developercloud/doc/conversation/convo_getstart.shtml).
- [Import a workspace](#workspace)
- Copy the [Service Credentials](#credentials) for later use. 
</a>

3 In Bluemix, [create a Retrieve and Rank Service](http://www.ibm.com/watson/developercloud/doc/retrieve-rank/get_start.shtml).
- Copy the [Service Credentials](#credentials) for later use.

## Running locally


1 Copy the `server.env` file from the `conversation-enhanced/src/main/resources` folder into the `<liberty install directory>/usr/servers/<server profile>/`

2 In the `server.env` file, search for **"retrieve_and_rank"**:
- Replace the "name" field with the name you gave your Retrieve and Rank Service.
- Replace the "password" field.
- Replace the "username" field.

3 In the `server.env`, search for **"conversation"**.
- Replace the "name" field with the name you gave your Conversation Service.
- Replace the "password" field.
- Replace the "username" field.

4 Add the **WORKSPACE_ID** that you [copied earlier](#workspaceID).

5 Start the server using Eclipse or CLI with the command `server run <server_profile>`.

6 Liberty notifies you when the server starts and includes the port information.

7 Open your browser of choice and go to the URL displayed in Step 6. By default, this is `http://localhost:9080/`.

<a name="credentials">
# Service Credentials
</a>

1 Go to the Bluemix Dashboard and select the Conversation service instance. Once there, select the **Service Credentials** menu item.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![](readme_images/credentials.PNG)

2 Select **ADD CREDENTIALS**. Name your credentials then select **ADD**.

<a name="workspace">
# Import a workspace
</a>

You need to import a workspace. 


1 Navigate to the Bluemix dashboard, select the service you created.

2 Go to the **Manage** menu item and select **Launch Tool**. This opens a new tab in your browser, where you are prompted to login if you have not done so before. Use your Bluemix credentials.

3 Download the [exported JSON file](src/main/resources/workspace.json) that contains the Workspace contents.

4 Select the import icon: ![](readme_images/importgGA.PNG) Browse to (or drag and drop) the JSON file that you downloaded in Step 3. Choose to import **Everything(Intents, Entities, and Dialog)**. Then select **Import** to finish importing the workspace.

5 Refresh your browser. A new workspace tile is created within the tooling. Select the _menu_ button within the workspace tile, then select **View details**:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![Workpsace Details](readme_images/details.PNG)

<a name="workspaceID">
In the Details UI, copy the 36 character UNID **ID** field. This is the **Workspace ID**.
</a>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ![](readme_images/workspaceid.PNG)

For more information on workspaces, see the full  [Conversation service  documentation](https://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/doc/conversation/overview.shtml).

7 Return to the deploy steps you were following:
- For Local - [return to step 2](#returnlocal)
- For Bluemix - [return to step 7](#returnbluemix)



<a name="env">
# Adding environment variables in Bluemix
</a>

1 In Bluemix, open the application from the Dashboard. Select **Environment Variables**.

2 Select **USER-DEFINED**.

3 Select **ADD**.

4 Add a variable with the name **WORKSPACE_ID**. For the value, paste in the Workspace ID you [copied earlier](#workspaceID). Select **SAVE**.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![](readme_images/env.PNG)

5 Restart your application.


# Troubleshooting in Bluemix

#### In the Classic Experience:
- Log in to Bluemix, you'll be taken to the dashboard.
- Navigate to the the application you previously created.
- Select **Logs**.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![](readme_images/logs.PNG)

- If you want, filter the LOG TYPE by "APP".

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![](readme_images/filter.PNG)

#### In the new Bluemix:
- Log in to Bluemix, you'll be taken to the dashboard.
- Select **Console** > **Compute**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![](readme_images/console.PNG)

- Select the application you previously created.
- Select **Logs**.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![](readme_images/logs1.PNG)

- If you want, filter the Log Type by selecting the drop-down and selecting **Application(APP)**.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![](readme_images/filter_app.PNG)

### With CLI

$ cf logs < application-name > --recent

# License

  This sample code is licensed under Apache 2.0.
  Full license text is available in [LICENSE](LICENSE).

# Contributing

  See [CONTRIBUTING](CONTRIBUTING.md).


## Open Source @ IBM

  Find more open source projects on the
  [IBM Github Page](http://ibm.github.io/).
