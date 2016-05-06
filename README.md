# FaceCrawler
mid automate login to facebook via android app

#Regenerate token

for regenerating new 60 days access token use following script written in node js:

//getNewToken.js

```groovy
//fetch graph: https://github.com/criso/fbgraph
var graph =require('fbgraph');

var client_id = 'client id of your app';
var client_secret = 'app secret';
var short_token = process.argv[2];
// extending specific access token
graph.extendAccessToken({
                        "access_token":    short_token
                        , "client_id":      client_id
                        , "client_secret": client_secret
                        }, function (err, facebookRes) {
                        
                        console.log('requesting for new token');
                        console.log(facebookRes);
                        });
```

then execute via Terminal:

```groovy
node getNewToken.js EAAHbBZCZBBbl8BAMGOWCLvSbfZCbyOX86MNg272pFVEoZCtmBy0qkgmawfAgV1wuUMv...
```
