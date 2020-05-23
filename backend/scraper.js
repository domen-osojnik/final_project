// SCRAPING:

/*
<tr class="PrometPanelItem" title="Prikaži na karti" style="cursor:pointer"
  onclick="window.ctl00_mainContent_ctl01_panelRowClick(&quot;b9d1ff24-ce57-41f9-8d65-30dd39e1156e&quot;)">
  <td style="width:40px; border-bottom:#9D9FA1 1px solid; text-align:center; vertical-align:middle"><img
          src="https://www.promet.si/portal/res/icons/dogodki/dogodek.png" style="width:28px; height:28px">
      <div style="width:40px;height:1px; visibility:hidden"></div>
  </td>
  <td
      style="width:80px; border-bottom:#9D9FA1 1px solid; text-align:center; vertical-align:middle; white-space:nowrap;">
      A2<div style="width:80px;height:1px; visibility:hidden"></div>
  </td>
  <td style="border-bottom:#9D9FA1 1px solid;">A2, Ljubljana - Karavanke, izvoz Lj. - Šentvid iz smeri Kosez, ovire na
      vozišču, oviran promet.</td>
</tr>
*/
module.exports = function() {

// Required modules
const puppeteer = require('puppeteer');
const request = require('request');
const $ = require('cheerio');
const md5 = require('md5');


// Program consts
const scraped_url = 'https://www.promet.si/portal/sl/razmere.aspx';
const server_url = 'http://127.0.0.1';
const server_port = 3000;
const server_path = '/scrapedEvent';
const timer = 10000; // 10s

// Container for hashed events
// (to compare which were changed)
var newHashes = Array();
var html;
var hashes = Array();
var i = 0;


// Using puppeteer to get dynamically loaded web page data (executing scripts etc.),
// then using cheerio to select useful data only

/*
 *  scrape
 *  scrape events from url, 
 *  build JSON file with data
 * 
 *  {
 *    "roadmark":"<mark of the road>",
 *    "type":"<type of the event>",
 *    "desc":"<event description>"
 *  }, ...
 * 
 */

console.log(">... Sending delete request");
  
request.delete(server_url + ":" + server_port + server_path, {}, function (error, response, body){
  console.log(">...  Delete finished, result: " + response.statusCode);
});

const scrape = async () => {

  jsonString = "{\"events\":[";

  // Get HTML
  const browser = await puppeteer.launch();
  const page = await browser.newPage();
  await page.goto(scraped_url);
  await page.$eval('#ctl00_mainContent_ctl00_LysChk_works', form => form.click());
  await page.$eval('#ctl00_mainContent_ctl00_SrtChk_updated', form => form.click());
  html = await page.content();

  // Scrape data
  await process()

  // Transmit
  if(i > 0) {
    await transmit();
    i = 0;
  }


  ; (async function () {
    // Scrape
    await process()
    // Send
    if(i > 0) await transmit();
  })()

  i = 0;
  
  // Restart function
  setTimeout(scrape, timer);

}

const transmit = async () => {
console.log(">... Sending data to server");

request.post(server_url + ":" + server_port + server_path, {
  json: true,
  body: JSON.parse(jsonString)
}, function (error, response, body){
  console.log(">...  Transmission finished, result: " + response.statusCode);
});


}


const process = async () => {
  try {
    console.log(">... Scraping")
    $('#ctl00_mainContent_ctl01_DogContainer table tbody .PrometPanelItem', html).each(function () {

      // Road, Description, Type ...
      var id = $(this).attr('onclick');
      var roadmark = $(this).find("td:nth-child(2)").text();
      var desc = $(this).find("td:nth-child(3)").text();
      var type = $(this).find("td:nth-child(1) img").attr('src').replace(/.*\//g, "").replace(/\..*/g, "");
      //console.log(roadmark + " " + desc + " " + type);
      var eventObj = "{\"roadmark\":\"" + roadmark + "\",\"desc\":\"" + desc + "\",\"type\":\"" + type + "\"},";

      // calculate object hash for comparison
      var currentHash = md5(id);
      newHashes.push(currentHash);

      var j = 0;
      for (; j < hashes.length; j++) {
        if (hashes[j] === currentHash) break;
      }

      if (hashes.length == j || hashes == null) {
        // Save JSON string
        jsonString += eventObj;
        i++;
      }

    })

    jsonString = jsonString.replace(/.$/, "]")
    jsonString += ", \"num\": \""+i+"\"}";
    hashes = newHashes;
    newHashes = Array();
    //console.log(jsonString);
    console.log(">... DONE (" + i + " new events registered)");
    return jsonString;
  }
  catch (exception) {
    console.log("Exception occured, exiting");
  }

}

// Call function
scrape();

};