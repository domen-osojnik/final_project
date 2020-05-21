var express = require('express');
var router = express.Router();
var scrapedEventController = require('../controllers/scrapedEventController.js');


/*
 * GET
 */
router.get('/', scrapedEventController.list);

/*
 * POST
 */
router.post('/', scrapedEventController.recieve);

/*
 * DELETE
 */
router.delete('/', scrapedEventController.clear);

module.exports = router;
