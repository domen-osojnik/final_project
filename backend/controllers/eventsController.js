var eventsModel = require('../models/eventsModel.js');
var mongoose = require('mongoose');

/**
 * eventsController.js
 *
 * @description :: Server-side logic for managing eventss.
 */
module.exports = {

    /**
     * eventsController.insert()
     */
    // Process recieved events
    insert: function (req, res, next) {
        console.log(req);
        var data = JSON.parse(req.body.data);
        console.log(data);
        console.log("\n>... recieved sensor readings (" + data.events.length + ")");
        res.locals.eventids = new Array();
        var i = 0;

        // For each of the sensor readings recieved
       // var bar = new Promise((resolve, reject) => {
            data.events.forEach(function (obj) {

                // Model the event
                var event = new eventsModel({
                    long: obj.long,
                    lat: obj.lat,
                    date: obj.date,
                    degree: obj.degree,
                    speed: obj.speed
                });

                // Save current evet to db
                event.save(function (err, result) {

                    if (err) {
                        return res.status(500).json({
                            message: 'Error when creating events',
                            error: err
                        });
                    }

                    // Remember its id for reference when creating a recording     
                    res.locals.eventids.push(mongoose.Types.ObjectId(result._id));
                    i++;
                    if (data.events.length == i) next();
                });

            });
    },

    /**
     * eventsController.clear()
     * delete all events (admin only)
     */
    clear: function (req, res) {
        eventsModel.remove({}, () => {
            return res.status(200).json({"msg":"removed all"});
        })
    },

    /**
     * eventsController.clear()
     * delete all events (admin only)
     */
    list: function (req, res) {
        eventsModel.find({}, (err, result) => {
            console.log(result);
            return res.status(200).json(result);
        })
    },


};
