var express = require('express');
var router = express.Router();
var recordingsController = require('../controllers/recordingsController.js');
var eventsController = require('../controllers/eventsController.js');
var imageController = require('../controllers/imageController.js');

var maxImgCount = 100;

var multer = require('multer');
var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'public/images/')
  },
  filename: function (req, file, cb) {
    cb(null, Date.now() + '.jpg') //Appending .jpg
  }
})
var upload = multer({ storage: storage });


/*
 * GET
 */
router.get('/', recordingsController.list);

/*
 * POST
 */
// Recieve serialized events from android application
//router.post('/', eventsController.insert, recordingsController.create);
router.post('/', upload.array('file[]',maxImgCount), imageController.insert, eventsController.insert, recordingsController.create);


/*
 * PUT
 */
//router.put('/:id', recordingsController.update);

/*
 * DELETE
 */
// delete all recordings (testing)
router.delete('/', recordingsController.clear);

module.exports = router;
