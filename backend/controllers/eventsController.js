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

        res.locals.ids = new Array();
        var i = 0;

        // For each of the sensor readings recieved
       // var bar = new Promise((resolve, reject) => {
            req.body.events.forEach(function (obj) {

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
                    res.locals.ids.push(mongoose.Types.ObjectId(result._id));
                    i++;
                    if (req.body.events.length == i) next();
                });

            });
    }

};
