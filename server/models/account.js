const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const accountSchema = new Schema({
    username: {
        type: String,
        required: true,
        unique: true,
        trim: true,
        minlength: 6,
        maxlength: 20
    },
    passwordHash: {
        type: String,
        required: true,
        minlength: 8,
        maxlength: 100
    },
    status: {
        type: String,
        enum: ['active', 'inactive', 'suspended'],
        default: 'active'
    },
    roleId: {
        type: Schema.Types.ObjectId,
        ref: 'Role',
        required: true
    },
    tutorId: { 
        type: Schema.Types.ObjectId, 
        ref: 'Tutor' 
    },
    clientId: { 
        type: Schema.Types.ObjectId, 
        ref: 'Client' 
    },
    staffId: { 
        type: Schema.Types.ObjectId, 
        ref: 'Staff'
    }
}, { timestamps: { createdAt: 'createdAt', updatedAt: 'updatedAt' } });

const Account = mongoose.model('Account', accountSchema);
module.exports = Account;
