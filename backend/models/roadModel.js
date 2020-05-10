var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var roadScheme = new Schema({
	'oznaka' : String,
	'avtocesta' : Boolean,
	'lokacijaID' : { type: Schema.Types.ObjectId, ref: 'location' },
	'stanjeID' : { type: Schema.Types.ObjectId, ref: 'condition' }
});

module.exports = mongoose.model('road', roadScheme); //ROAD
