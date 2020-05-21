var scrapedEventModel = require('../models/scrapedEventModel.js');

/**
 * scrapedEventController.js
 *
 * @description :: Server-side logic for managing scrapedEvents.
 */
module.exports = {



    /**
     * scrapedEventController.create()
     * recieve scraped events from scraper,
     * insert them to DB
     */
    recieve: function (req, res) {
    
        console.log(">... POST: /scrapedEvents -- Recieved " + req.body.num + " events");

        var i = 0;
        req.body.events.forEach(function (event) {

            var scrapedEvent = new scrapedEventModel({
                roadmark : event.roadmark,
                type : event.type,
                desc : event.desc,
            });

            scrapedEvent.save(function (err, result) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when creating events',
                        error: err
                    });
                }
                i++;

                if (req.body.num == i) {
                    return res.status(200).json({"msg":"ok"});
                }
            });
        })
    },


      /**
     * scrapedEventController.clear()
     * delete all scraped events (admin only)
     */
    clear: function (req, res) {
        scrapedEventModel.remove({}, () => {
            return res.status(200).json({"msg":"removed all"});
        })
    },


      /**
     * list all scraped events
     */
    list: function (req, res) {
        scrapedEventModel.find({}, function(err, data) {
            return res.status(200).json(data);
        })
    },
};
