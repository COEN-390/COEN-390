using System;
using System.Threading.Tasks;
using System.Collections.Generic;
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
            var eventData = Environment.GetEnvironmentVariable("APPWRITE_FUNCTION_DATA");
            //dynamic eventDataJson = JObject.Parse(eventData);

            // Grabs the GOOGLE_APPLICATION_CREDENTIALS from the environment variable to authenticate to FCM
            var messaging = FirebaseMessaging.GetMessaging(FirebaseApp.Create(new AppOptions()
            {
                Credential = GoogleCredential.GetApplicationDefault(),
            }));

            // Sends the notification
            string response = await messaging.SendAsync(new Message()
            {
               
                Data = new Dictionary<string, string>()
                {
                    {"Title", "No Mask Detected!!"},
                    {"Body", eventData},
                    {"ImageUrl", "https://icons.iconarchive.com/icons/paomedia/small-n-flat/256/cat-icon.png"}
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