var eventsModel = require('../models/eventsModel.js');
var mongoose = require('mongoose');

/**
 * eventsController.js
 *
 * @description :: Server-side logic for managing eventss.
 */
module.exports = {

    /**
     * eventsController.list()
     */
    list: function (req, res) {
        eventsModel.find(function (err, eventss) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting events.',
                    error: err
                });
            }
            return res.json(eventss);
        });
    },

    /**
     * eventsController.show()
     */
    show: function (req, res) {
        var id = req.params.id;
        eventsModel.findOne({ _id: id }, function (err, events) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting events.',
                    error: err
                });
            }
            if (!events) {
                return res.status(404).json({
                    message: 'No such events'
                });
            }
            return res.json(events);
        });
    },

    /**
     * eventsController.create()
     */
    create: function (req, res) {
        var events = new eventsModel({
            long: req.body.long,
            lat: req.body.lat,
            date: req.body.date,
            degree: req.body.degree,
            speed: req.body.speed

        });

        events.save(function (err, events) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating events',
                    error: err
                });
            }
            return res.status(201).json(events);
        });
    },

    /**
     * eventsController.update()
     */
    update: function (req, res) {
        var id = req.params.id;
        eventsModel.findOne({ _id: id }, function (err, events) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting events',
                    error: err
                });
            }
            if (!events) {
                return res.status(404).json({
                    message: 'No such events'
                });
            }

            events.long = req.body.long ? req.body.long : events.long;
            events.lat = req.body.lat ? req.body.lat : events.lat;
            events.date = req.body.date ? req.body.date : events.date;
            events.degree = req.body.degree ? req.body.degree : events.degree;
            events.speed = req.body.speed ? req.body.speed : events.speed;

            events.save(function (err, events) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating events.',
                        error: err
                    });
                }

                return res.json(events);
            });
        });
    },

    /**
     * eventsController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;
        eventsModel.findByIdAndRemove(id, function (err, events) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the events.',
                    error: err
                });
            }
            return res.status(204).json();
        });
    },

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
