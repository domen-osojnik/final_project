var express = require('express');
var router = express.Router();
var recordingsController = require('../controllers/recordingsController.js');
var eventsController = require('../controllers/eventsController.js');


/*
 * GET
 */
router.get('/', recordingsController.show);

/*
 * POST
 */
// Recieve serialized events from android application
router.post('/', eventsController.insert, recordingsController.create);

/*
 * PUT
 */
router.put('/:id', recordingsController.update);

/*
 * DELETE
 */
router.delete('/:id', recordingsController.remove);

module.exports = router;
