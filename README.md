# New issue action
* Opens a dialog window in which the created branch is displayed in the format 
rr / {user nick} / {issue id} / {short description}
* The first time a branch is created in the current project, the user's nickname is saved in user settings. 
At the next branch creation in the project, the saved user nickname will be displayed in the corresponding field.
* The issue id field is the ComboBox for which data is taken from YouTrack services configured in the YouTrack plugin. 
Therefore, in order for the issue list to be displayed, you must first configure the YouTrack plugin in your project. 
Issues in the list are displayed only for a current user ("for: me"). Also in this field you can enter the issue 
id yourself, from the list of ComboBox, suitable issues will be offered.
* Depending on the selected issue id, a line from the issue summary is generated in the short description field. 
For this, the first 10 words (if there are more than 10) of the summary are taken and connected through a dot. 
If the text of this field does not suit the user, he can change it.
* Below, under the created branch, a full description of the selected issue is displayed.
* After selecting a issue and creating a branch, the issue status becomes "In Process".

![create branch](https://github.com/Elmo397/kotlin-process-plugin/blob/master/src/main/resources/readmeFiles/CreateBranch.gif)

# Remote Run  
* When the remote run check is started, all branches of the current user are checked 
(user can start check rr, but can't stop checking).
* Checks are carried out every fixed number of seconds that can be configured (default is every 120 seconds).
* If everything is fine in the current branch and user finished fixing the issue, user can create the final commit.
* If not, then when user try to create the final commit, the user will be warned that the tests failed.

![rr settings](https://github.com/Elmo397/kotlin-process-plugin/blob/master/src/main/resources/readmeFiles/RrSettings.png)

# Review bot
* In Process... (may be)

# Final commit action
* Final commit has a default text: denotation of the fixed issue and selection reviewer's name. 
User can write your message above.
* After creating the final commit, the status of the issue changes to "Fixed".

![final commit](https://github.com/Elmo397/kotlin-process-plugin/blob/master/src/main/resources/readmeFiles/StateToFixed.gif)

# Merge to master / release branch action
* In dialog window user can choose branch for merge to master.
* It is proposed to merge only those branches whose state is "Fixed".

![merge](https://github.com/Elmo397/kotlin-process-plugin/blob/master/src/main/resources/readmeFiles/Merge.gif)
