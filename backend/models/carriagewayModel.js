var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var carriagewayScheme = new Schema({
	'lokacijaID' : { type: Schema.Types.ObjectId, ref: 'location' },
	'stanjeID' : { type: Schema.Types.ObjectId, ref: 'condition' }
});

module.exports = mongoose.model('carriageway', carriagewayScheme); //VOZIŠČE
