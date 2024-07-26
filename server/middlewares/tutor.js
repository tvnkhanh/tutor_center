const jwt = require('jsonwebtoken');
const Account = require('../models/account');
const Role = require('../models/role');

const tutor = async (req, res, next) => {
    try {
        const token = req.header("x-auth-token");
        if (!token)
          return res.status(401).json({ msg: "No auth token, access denied!" });
    
        const verified = jwt.verify(token, process.env.JWT_SECRET);
        if (!verified)
          return res
            .status(401)
            .json({ msg: "Token verification failed, authorization denied." });
        
        const account = await Account.findById(verified.id);
        const role = await Role.findById(account.roleId);

        if (role.roleName == process.env.ADMIN_ROLE || role.roleName == process.env.CLIENT_ROLE) {
            return res.status(401).json({ msg: "You are not an Tutor!" });
        }
        req.account = verified.id;
        req.token = token;
        next();
      } catch (err) {
        res.status(500).json({ error: err.message });
      }
}

module.exports = tutor;
