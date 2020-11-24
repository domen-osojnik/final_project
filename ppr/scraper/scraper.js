const fs = require('fs');
const puppeteer = require('puppeteer');
const $ = require('cheerio');
const { exit } = require('process');

const url = 'https://www.promet.si/portal/sl/stevci-prometa.aspx';
const selector = "#ctl00_mainContent_ctl00_StvContainer table tbody"

function validate(value) {
  return value === "" ? 0 : value;
}

const process = async (html) => {
  try {

    var stevilo, hitrost, razmik, items = $(`${selector} .PrometPanelItem`, html);
    fs.writeFileSync("out", `${items.length}\n`);

    items.each(function () {

      stevilo = validate ($(this).find("td:nth-child(6)").text());
      hitrost = validate ($(this).find("td:nth-child(7)").text());
      razmik = validate ($(this).find("td:nth-child(8)").text());

      fs.writeFileSync("out", 
      `${stevilo} ${hitrost} ${razmik}\n`, 
      { 
        flag: "a+", 
      }); 
    });
    
  }
  catch (e) {
    console.log(`Exception:\n${e}`);
  }
}

const scrape = async () => {
  try {
    const browser = await puppeteer.launch();
    const page = await browser.newPage();
    await page.goto(url);
    await page.waitForSelector(selector);
    var html = await page.content();
    await process (html);
    exit();
  }
  catch (e) {
    console.log(`Exception:\n${e}`);
  }
}

scrape();
