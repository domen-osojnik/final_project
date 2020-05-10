var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var eventScheme = new Schema({
	'naziv' : String,
	'lokacijaID' : { type: Schema.Types.ObjectId, ref: 'location' }
});

module.exports = mongoose.model('event', eventScheme); //DOGODEK
