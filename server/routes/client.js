const express = require("express");
const Client = require('../models/client');
const Account = require("../models/account");
const auth = require('../middlewares/auth');

const clientRouter = express.Router();

clientRouter.post('/api/client', auth, async (req, res) => {
    try {
        const account = await Account.findById(req.account);
        const { firstName, lastName, contactNumber, email } = req.body;

        if (account.clientId) {
            let client = await Client.findById(account.clientId);
            client.firstName = firstName;
            client.lastName = lastName;
            client.contactNumber = contactNumber;
            client.email = email;

            await client.save();

            return res.status(200).json({ message: 'Client updated successfully', client });
        }

        const clientData = new Client({
            firstName,
            lastName,
            contactNumber,
            email,
        });

        await clientData.save();
        
        const clientId = clientData._id;

        account.clientId = clientId;
        await account.save();

        res.status(201).json({ message: 'Client created successfully', client: clientData });
    } catch (error) {
        console.error(error.message);
        res.status(500).json({ error: 'Failed to create / update client data' });
    }
});

clientRouter.get('/api/client/get', auth, async (req, res) => {
    try {
        const account = await Account.findById(req.account);
        
        const client = await Client.findById(account.clientId);
        if (!client)
            return res.status(404).json({ message: 'Client not found' });

        res.status(200).json({ client });
    } catch (error) {
        console.error(error.message);
        res.status(500).json({ error: 'Failed to get client data' });
    }
});

module.exports = clientRouter;