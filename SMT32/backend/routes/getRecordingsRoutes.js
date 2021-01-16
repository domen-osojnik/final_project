var express = require('express');
var router = express.Router();
var recordingsController = require('../controllers/recordingsController.js');
var eventsController = require('../controllers/eventsController.js');

/*
 * GET
 */
router.get('/', recordingsController.list);

module.exports = router;
