Autism-Diagnose
===============

Autism Diagnosis app

Setup
-----


Download Eclipse.
Using Eclipse, [Install Android Plugin.](http://developer.android.com/sdk/installing/installing-adt.html)

SDK Manager:
http://developer.android.com/tools/help/sdk-manager.html

Install each of the recommended libraries in the [SDK manager](http://developer.android.com/tools/help/sdk-manager.html
), as well as android 22, 20, and 19.


Note: At each of the following steps, you may see errors in your workspace. Many of these errors will be resolved after completing all of following instructions.

Create a workspace to contain this project's necessary Libraries. Inside it:


1. Copy extras/android/support/v7/appcompat from Android folder (sdk location)

- Go to File -> Import -> Android -> Existing Android Code and select the folder for "appcompat"

   
2. Clone ViewPagerIndicator from Github: https://github.com/JakeWharton/ViewPagerIndicator.git

- Go to File -> Import -> Android -> Existing Android Code and select the folder for "ViewPagerIndicator"
- Go to Project -> Properties -> Android and check the box next to "is library".

   
3. Clone Autism-Diagnose from Github: https://github.research.chop.edu/cbmi/Autism-Diagnose

- Checkout branch: "changes-to-pic-and-home-screen"
- Go to New -> Project -> Android Project from Existing Code and select the folder for "Autism Diagnose"
- Go to Project -> Properties -> Android and Add "library" and "appcompat"
- Go to Project -> Properties -> Java Build Path -> "Order and Export" and uncheck "joda-time.jar"
- Note: the previous step may not be necessary in all cases. If it is joda-time is not listed, ignore this step. 

In each of the 3 above:
   Go to project.properties and set android version to 22 (or another version, as long as   they are the same)
