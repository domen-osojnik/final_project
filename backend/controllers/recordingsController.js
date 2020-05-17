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
     * recordingsController.show()
     */
    show: function (req, res) {
        var id = req.params.id;
        recordingsModel.findOne({ _id: id }, function (err, recordings) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting recordings.',
                    error: err
                });
            }
            if (!recordings) {
                return res.status(404).json({
                    message: 'No such recordings'
                });
            }
            return res.json(recordings);
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

    /**
     * recordingsController.update()
     */
    update: function (req, res) {
        var id = req.params.id;
        recordingsModel.findOne({ _id: id }, function (err, recordings) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting recordings',
                    error: err
                });
            }
            if (!recordings) {
                return res.status(404).json({
                    message: 'No such recordings'
                });
            }

            recordings.events = req.body.events ? req.body.events : recordings.events;
            recordings.date = req.body.date ? req.body.date : recordings.date;

            recordings.save(function (err, recordings) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating recordings.',
                        error: err
                    });
                }

                return res.json(recordings);
            });
        });
    },

    /**
     * recordingsController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;
        recordingsModel.findByIdAndRemove(id, function (err, recordings) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the recordings.',
                    error: err
                });
            }
            return res.status(204).json();
        });
    }
};
