const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.pushNotifications = functions.database.ref("classAnnouncements/{ClassXX}/{postID}/").onWrite(async (change, context) => {

    //const classNotif = event.params.ClassXX;
    
    const postID = context.params.postID;
    const classxx = context.params.ClassXX;
    console.log("Push notification for the postID : " + postID + " : and for Class : " + classxx);


    var valueObject = change.after.val();



    var title = valueObject.title;
    console.log("title is : " + title);


    const payload = {
        notification: {
            title: valueObject.title,
            body:  valueObject.desc
        },
    };

    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };

    return admin.messaging().sendToTopic(classxx + "_Announcements", payload, options);
});

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
