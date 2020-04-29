
# Welcome to FreeStyleLibrePlugin for Cordova!  
  
Hi! FreeStyleLibrePlugin is a Cordova plugin to connect to FreeStyle Libre sensors.  
This project is created as part of thesis work project.  
  
## FreeStyleLibrePlugin  
FreeStyleLibrePlugin is for connecting and reading the data from FreeStyle Libre using NFC.  
FreeStyleLibrePlugin supports `Android` devices only.  
  
### Permissions  
- NFC access  
- Vibrator access  
### Installation  
Latest stable version from master brunch  
  
$ionic corova plugin add [https://github.com/BilelKorbosli/Thesis_cordova_plugin.git](https://l.facebook.com/l.php?u=https%3A%2F%2Fgithub.com%2FBilelKorbosli%2FThesis_cordova_plugin.git%3Ffbclid%3DIwAR36YoSWPut1BnWMl2ZqMNYJBurQ0xSndcYrUxXZBuhhGhAkUyWmDqh2x80&h=AT0huUG9ORhdHBM_x3J5YWNRF_RqlZJ2zTyOUFVmOfHM_mJOFDu6NL8lsVgVJ7-CQLcr20lpbAcAvxD84tJyJJb9UII-TvKlf6RySNsHsvBeGSlv37VOyxvE3R4in_W3YDR8FAQUAJFDTQ)  
  
### Usage  
  

  
Make sure to wait for `deviceready` before using any of these functions.

#### Declaration

    declare  var  window:any;

#### Check NFC is enabled

    window.nfc.enabled(successCallback, errorCallback)

This function is to check if the NFC is enabled or not, we cannot proceed of the NFC is disabled.
On success:
 - `NFC_OK`
 
On failure:
 - `NFC_DISABLED`
  - `NO_NFC` The device does not support NFC technology

**Sample:**


```autohotkey
function onSuccess() {
    console.log("NFC is enabled.")
}
function onFailure(message) {
    alert('Failed because: ' + message);
}
window.nfc.enabled(onSuccess, onFailure);
```

#### Add Tag discover listener

    window.nfc.addTagDiscoveredListener(successCallback, errorCallback);
    
This  Function is for adding a tag listener on NFC reader. successCallback trigger when a the phone detect a sensor.
On sucess:

 - `data`  contain sensor info
 - `data.tag` we are interested in the device `tag` to connect

Sample:
```autohotkey
var  onSuccess = function(data) {
console.log(JSON.stringify(data, undefined, 2));
}
var  onFailure = function(err) {
console.log(JSON.stringify(err, undefined, 2));
}
window.nfc.addTagDiscoveredListener(onSuccess,OnFailure);
```
#### Connect

    window.nfc.connect(tech, timeout).then(onSuccess,OnFailure)
This function is to connect to the Freestyle Libre sensor.
Args: 

 - `tech` TagTechnology class name e.g. 'android.nfc.tech.IsoDep' or 'android.nfc.tech.NfcV'
 - `timeout` in milliseconds

On success:

 - `Call the success callback function`

On failure

 - `Call the failure callback function with error msg`
Sample:
```autohotkey
function onSuccess() {
    console.log("Connected")
}
function onFailure(message) {
    alert('Failed because: ' + message);
}
window.nfc.connect(data.tag.techTypes[0],500).then(()=>onSuccess,(error)=>onFailure);
```
#### Read data from the sensor

    window.nfc.dumpData().then(successCallback).catch(failureCallback)
This function is to dump the data from the sensor and covert it to a readable format.
onSuccess:
	

 - {"`allDump`":"Contain the memory dump on hex forma"}

	

 - {"`GlucoseVal`": ["`GVal`":"Glucose level",
   "`TVal`":"Timestamp"],....}

onFailure:
- `Call the failure callback function with error msg`

Sample:
```autohotkey
function onSuccess(data) {
    console.log(JSON.stringify(data, undefined, 2));
}
function onFailure(message) {
    alert('Failed because: ' + message);
}
window.nfc.dumpData().then((data)=>onSuccess(data)).catch((error)=>onFailure(error));
```
