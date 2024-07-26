const express = require("express");
const bcryptjs = require("bcryptjs");
const jwt = require("jsonwebtoken");
const dotenv = require('dotenv');

const Account = require("../models/account");
const Role = require("../models/role");
const auth = require("../middlewares/auth")

const authRouter = express.Router();

dotenv.config();

// Sign Up
authRouter.post("/api/signup", async (req, res) => {
  try {
    const { username, passwordHash, roleId } = req.body;

    const existingAccount = await Account.findOne({ username });
    if (existingAccount) {
      return res
        .status(400)
        .json({ msg: "Account with same username already exists" });
    }

    const hashedPassword = await bcryptjs.hash(passwordHash, 8);

    let account = new Account({
      username,
      passwordHash: hashedPassword,
      roleId: roleId,
    });
    
    account = await account.save();
    res.json(account);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// Sign In
authRouter.post("/api/signin", async (req, res) => {
  try {
    const { username, passwordHash } = req.body;

    const account = await Account.findOne({ username });
    if (!account) {
      return res
        .status(400)
        .json({ msg: "Account with this username does not exists!" });
    }

    const isMatch = await bcryptjs.compare(passwordHash, account.passwordHash);
    if (!isMatch) {
      return res.status(400).json({ msg: "Wrong password!" });
    }

    const token = jwt.sign({ id: account._id }, process.env.JWT_SECRET, { expiresIn: '30d' });
    res.json({ token, ...account._doc });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

authRouter.post("/tokenIsValid", async (req, res) => {
  try {
    const token = req.header("x-auth-token");
    if (!token) return res.json(false);
    const verified = jwt.verify(token, process.env.JWT_SECRET);
    if (!verified) return res.json(false);

    const account = await Account.findById(verified.id);
    if (!account) return res.json(false);
    return res.json(true);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

authRouter.get("/", auth, async (req, res) => {
  const account = await Account.findById(req.account);
  res.json({ ...account._doc, token: req.token });
});

module.exports = authRouter;
