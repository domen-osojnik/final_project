var recordingsModel = require('../models/recordingsModel.js');

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
        console.log("\n>... recieved sensor readings (" + req.body.events.length + ")");
        console.log();
        var i = 0;

        console.log(req.body.events);
        var recordings = new recordingsModel({
            events: res.locals.ids, //Fix this
            date: req.body.date
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
};
