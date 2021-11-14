using System;
using System.Threading.Tasks;
using FirebaseAdmin;
using FirebaseAdmin.Messaging;
using Google.Apis.Auth.OAuth2;
using Newtonsoft.Json.Linq;

namespace FCMPushNotifications
{
    class Program
    {
        static async Task Main(string[] args)
        {
            // Contains the JSON passed to the cloud function
            //var eventData = Environment.GetEnvironmentVariable("APPWRITE_FUNCTION_EVENT_DATA");
            //dynamic eventDataJson = JObject.Parse(eventData);

            // Grabs the GOOGLE_APPLICATION_CREDENTIALS from the environment variable to authenticate to FCM
            var messaging = FirebaseMessaging.GetMessaging(FirebaseApp.Create(new AppOptions()
            {
                Credential = GoogleCredential.GetApplicationDefault(),
            }));

            // Sends the notification
            string response = await messaging.SendAsync(new Message()
            {
                Notification = new Notification()
                {
                    //Authorization="AAAA052hQHY:APA91bGyWmgzuCsGyO3eC5mex9mOmrgTEtUG5Dk5ffupNCN_KfLVGJk3hlUp4Oi7mECmyVp3KDImMhI3MtKVziLoh6Gb1zs21Vwp8Bv2Wbe2ZJ858fO1js16yeZ0VtQG_8v4Vvv8CvwV",
                    Title = "Test title",
                    Body = "test body",
                    ImageUrl = "https://icons.iconarchive.com/icons/paomedia/small-n-flat/256/cat-icon.png"
                },
                Android = new AndroidConfig()
                {
                    Priority = Priority.High,
                },
                
                Topic = "/topics/UsersA"
            });
            
            Console.WriteLine("Successfully sent message: " + response);
        }   
    }
}