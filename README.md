# facerecognition_application

# Install Dependencies
1. Install dlib library by following this [guide](https://www.pyimagesearch.com/2018/01/22/install-dlib-easy-completeguide/)
2. Install all other packages using the command `pip install -r requirements.txt`
3. Install flask in virtual environment. Reference link as follows https://flask.palletsprojects.com/en/1.1.x/installation/#installation
4. Install Android Studio.

# After installing the prerequesites, follow the steps
1. Create a folder `mydatabase` which has several folders each having photos of a
single person and the folder name being the identity (name) of that person. Check
the sample `dataset3` folder in the repository.
2.Create high level embeddings from these images using the deep learning model and store it in pickle file. To do this just run ` python encode_faces.py -e
my_encodings.pickle -i mydatabase -d hog` . This will create a file called `my_encodings.pickle`. Details about other options are explained in the python script.
3. Open the project named GetAttendace in Android Studio. In mainactivity.java in connectServer method change the “postUrl” string to “http://ipv4address:5000/ “ or server url And in connectServer1 method change the “myurl1” string to “http://ipv4address:5001/ “ or server url
4. Run the application with encoding file as an argument. `python app.py -e my_encodings.pickle -d hog` in terminal.
5. Open another terminal and run toupdate.py 
6. Run the app the in the android studio using emulator or install the app in your phone by using connector cable.
7. Open the app in phone/emulator and click “PICK IMAGES” to select images of the class from the Gallery.
8. After selecting the images click “connect to server ” to get absentees list of the class.
9. If any of the student is present but still marked absent, select those student names and “Update the Attendence”.
10. After getting correct absentees list click “Done with Attendence” button.
