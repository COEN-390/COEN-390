using System;
using System.Threading.Tasks;
using FirebaseAdmin;
using FirebaseAdmin.Messaging;
using Google.Apis.Auth.OAuth2;
using Newtonsoft.Json.Linq;
using System.Collections.Generic;

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


            // These registration tokens come from the client FCM SDKs.
            // Currently subscribed both Facundo and Fahim's Virtual Phones 
            var registrationTokens = new List<string>()
            {
                eventData,   

                //Facundo
                //"esW8BiP1RmOav4ekyquCtN:APA91bG9tHC0IyrTcgocFQt2XrOfPtJ_bQuM4vDPHslrEgNREsn1P-sfkDHW0deVZJassFMBNgdRjhbofwYukp740AIJZHvwWfhQKCVDH0zDfTWaKpMeb0RWa0Gc6y8PXvNUGGcpJ1lK",
                //Fahim
                //"f_z_KVzQSwCigRUaWyIPQ0:APA91bEolTuAOtE_Z92tm6e-qWzAIkcr6s6S-fcoLAhYCm14At3czUZG9bKMfOj8VBnFffSz2rVbx6L6_Mr7Dw9JHDxek3SbRFeIrSQgBojWJ2ZHt3Nva4bOzRhzGit2Af4_RcmZ321g",             
            };

            var topic = "/topics/UsersA"; 

            // Subscribe the devices corresponding to the registration tokens to the
            // topic
            var response = await messaging.SubscribeToTopicAsync(
                registrationTokens, topic);
            // See the TopicManagementResponse reference documentation
            // for the contents of response.
            Console.WriteLine($"{response.SuccessCount} tokens were subscribed successfully");
        }   
    }
}