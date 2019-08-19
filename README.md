#New task action
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

#Remote Run  
* When the remote run check is started, all branches of the current user are checked.
* Checks are carried out every fixed number of seconds that can be configured (default is every 120 seconds).
* If everything is fine in the current branch, then a message arrives that the user can create a Pull Request.
* If not, then when you try to create a Pull Request, the user will be warned that the tests failed.

#Pull Request action
* This action performs a pull request to the master.
* The description field displays the issue that was completed, and you must write a reviewer. Autocomplete 
is implemented to select a reviewer

#Review bot
* ...

#Merge to master / release branch action
* After review is finished, a dialog window is shown for user once a day with a proposal to merge his branch with master.
* If Ok user branch merge in master.