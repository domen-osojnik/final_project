var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var driveFeedScheme = new Schema({
	'znakID' : { type: Schema.Types.ObjectId, ref: 'sign' },
	'opis' : String
});

module.exports = mongoose.model('drive_feed', driveFeedScheme); //VOŽNJA_FEED
