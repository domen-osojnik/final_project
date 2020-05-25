var signModel = require('../models/signModel.js');
var imageModel = require('../models/imageModel.js');
//var mongoose = require('mongoose');

module.exports = {
    list: function (req, res) {
        imageModel.find(function (err, images) {
            images.forEach(function(image) {
            console.log(image),
            unix_timestamp = image.path.slice(0,-4);
            var date = new Date(unix_timestamp * 1000);
            // Hours part from the timestamp
            var hours = date.getHours();
            // Minutes part from the timestamp
            var minutes = "0" + date.getMinutes();
            // Seconds part from the timestamp
            var seconds = "0" + date.getSeconds();
            console.log(date.toJSON());
            
            });
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting image.',
                    error: err
                });
            }
            return res.json(images);
        });
/*
        signModel.find(function (err, signs) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting image.',
                    error: err
                });
            }
            return res.json(signs);
        });
*/
    },
    clear: function (req, res) {
        var id = req.params.id;
        signModel.remove({}, function (err, result) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting images.',
                    error: err
                });
            }
            return res.status(200).json({ "msg": "ok" });
        });
    }
};
