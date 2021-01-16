var imageModel = require('../models/imageModel.js');
var mongoose = require('mongoose');

/**
 * imageController.js
 *
 * @description :: Server-side logic for managing images.
 */
module.exports = {

    /**
     * imageController.list()
     */
    list: function (req, res) {
        imageModel.find(function (err, images) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting image.',
                    error: err
                });
            }
            return res.json(images);
        });
    },


    /**
     * imageController.insert()
     *  Insert - add img. path to db, store img on server
     */
    insert: function (req, res, next) {
        console.log(">...Recieved " + req.files.length + " images");

        res.locals.imgids = new Array();
        var i = 0;

        // For each of the images recieved
        req.files.forEach(function (file) {

            // Model the image
            var image = new imageModel({
                path: file.filename
            });

            // Save current evet to db
            image.save(function (err, result) {

                if (err) {
                    return res.status(500).json({
                        message: 'Error when creating images',
                        error: err
                    });
                }

                // Remember its id for reference when creating a recording     
                res.locals.imgids.push(mongoose.Types.ObjectId(result._id));
                i++;
                if (req.files.length == i) next();
            });

        });
    },

    /**
     * imageController.clear()
     * Clear - delete all photos (testing)
     */
    clear: function (req, res) {
        var id = req.params.id;
        imageModel.remove({}, function (err, result) {
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
