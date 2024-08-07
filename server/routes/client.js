const express = require("express");
const Client = require("../models/client");
const Account = require("../models/account");
const auth = require("../middlewares/auth");

const clientRouter = express.Router();

// clientRouter.post("/api/client", async (req, res) => {
//   try {
//     const account = await Account.findById(req.account);
//     const { firstName, lastName, contactNumber, email } = req.body;

//     if (account.clientId) {
//       let client = await Client.findById(account.clientId);
//       client.firstName = firstName;
//       client.lastName = lastName;
//       client.contactNumber = contactNumber;
//       client.email = email;

//       await client.save();

//       return res
//         .status(200)
//         .json({ message: "Client updated successfully", client });
//     }

//     const clientData = new Client({
//       firstName,
//       lastName,
//       contactNumber,
//       email,
//     });

//     await clientData.save();

//     const clientId = clientData._id;

//     account.clientId = clientId;
//     await account.save();

//     res
//       .status(201)
//       .json({ message: "Client created successfully", client: clientData });
//   } catch (error) {
//     console.error(error.message);
//     res.status(500).json({ error: "Failed to create / update client data" });
//   }
// });

clientRouter.post('/api/client', async (req, res) => {
  try {
      const { firstName, lastName, contactNumber, email } = req.body;

      const newClient = new Client({
          firstName,
          lastName,
          contactNumber,
          email,
      });

      await newClient.save();

      res.status(201).json({ message: 'Client created successfully', client: newClient });
  } catch (error) {
      console.error(error.message);

      if (error.code === 11000) {
          if (error.keyPattern.contactNumber) {
              return res.status(400).json({ error: 'Contact number already exists' });
          }
          if (error.keyPattern.email) {
              return res.status(400).json({ error: 'Email address already exists' });
          }
      }

      res.status(500).json({ error: 'Failed to create client' });
  }
});

clientRouter.get("/api/client/get", auth, async (req, res) => {
  try {
    const account = await Account.findById(req.account);

    const client = await Client.findById(account.clientId);
    if (!client) return res.status(404).json({ message: "Client not found" });

    res.status(200).json(client);
  } catch (error) {
    console.error(error.message);
    res.status(500).json({ error: "Failed to get client data" });
  }
});

clientRouter.get("/api/get-client", auth, async (req, res) => {
  try {
    const client = await Client.findById(req.query.clientId);
    if (!client) {
      return res.status(404).json({ message: "Client not found" });
    }
    res.status(200).json(client);
  } catch (e) {
    res.status(500).json({ message: error.message });
  }
});

clientRouter.get('/api/client-id', async (req, res) => {
  const { contactNumber } = req.query;

  try {
      const client = await Client.findOne({ contactNumber: contactNumber }).select('_id');
      if (!client) {
          return res.status(404).send({ message: 'Client not found' });
      }
      res.send(client._id);
  } catch (error) {
      res.status(500).send({ message: 'Server error', error: error.message });
  }
});

module.exports = clientRouter;
