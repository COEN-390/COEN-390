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
            var eventData = Environment.GetEnvironmentVariable("APPWRITE_FUNCTION_EVENT_DATA");
            dynamic eventDataJson = JObject.Parse(eventData);

            // Grabs the GOOGLE_APPLICATION_CREDENTIALS from the environment variable to authenticate to FCM
            var messaging = FirebaseMessaging.GetMessaging(FirebaseApp.Create(new AppOptions()
            {
                Credential = GoogleCredential.GetApplicationDefault(),
            }));

            // Sends the notification
            await messaging.SendAsync(new Message()
            {
                Notification = new Notification()
                {
                    Title = "Test title",
                    Body = "test body",
                    ImageUrl = "https://icons.iconarchive.com/icons/paomedia/small-n-flat/256/cat-icon.png"
                },
                Android = new AndroidConfig()
                {
                    Priority = Priority.High,
                },
                //Topic = "/topics/some_topic",
                //Token = "d8sG......OApP3"// token taken from FirebaseMessaging.instance.getToken()
            });
        }
    }
}