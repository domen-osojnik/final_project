var express = require('express');
var router = express.Router();
var eventsController = require('../controllers/eventsController.js');

/*
 * GET
 */
router.get('/', eventsController.list);

/*
 * GET
 */
//router.get('/:id', eventsController.show);

/*
 * POST
 */
router.post('/', eventsController.insert);
/*
 * PUT
 */
//router.put('/:id', eventsController.update);

/*
 * DELETE
 */
router.delete('/', eventsController.clear);

module.exports = router;
