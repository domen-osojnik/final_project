var recordingsModel = require('../models/recordingsModel.js');
var mongoose = require('mongoose');

/**
 * recordingsController.js
 *
 * @description :: Server-side logic for managing recordingss.
 */
module.exports = {

    /**
     * recordingsController.list()
     */
    list: function (req, res) {
        recordingsModel.find().sort({date:-1}).populate('events').exec(function (err, recordingss) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting recordings.',
                    error: err
                });
            }
            return res.json(recordingss);
        });
    },

    /**
     * recordingsController.create()
     */
    create: function (req, res) {
        
        var data = JSON.parse(req.body.data);
        var i = 0;
       
        //console.log(req.body.data.events);
        var recordings = new recordingsModel({
            events: res.locals.eventids,    // saved event ids -> controllers/eventsController/insert
            images: res.locals.imgids,       // saved image ids -> controllers/imageController/insert
            date: data.date,
        });

        recordings.save(function (err, recording) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating recordings',
                    error: err
                });
            }
            return res.status(200).json({ "msg": "ok" });
        });

    },

  
   clear: function (req, res) {
       recordingsModel.remove({}, function (err, result) {
           if (err) {
               return res.status(500).json({
                   message: 'Error when deleting recordings.',
                   error: err
               });
           }
           return res.status(200).json({"msg":"ok"});
       });
   },
};
