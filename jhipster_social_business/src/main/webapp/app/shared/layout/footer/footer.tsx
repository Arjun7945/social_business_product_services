import './footer.scss';

import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Col, Row } from 'reactstrap';

const Footer = () => (
  <div className="footer text-center mt-3">
    <div>
      <img src="content/images/footerlogo.jpg" alt="Logo" width="50" className="mb-2" />
    </div>
    <p className="mb-1">powered by LXI Technologies</p>
    <p className="mb-2">Palakkad | Kerala | India</p>

    <div className="mb-1" style={{ display: 'flex', gap: '15px', justifyContent: 'center', alignItems: 'center' }}>
      <span>
        <FontAwesomeIcon icon="phone" className="me-1" /> +91 7736647856
      </span>
      <span>|</span>
      <span>
        <FontAwesomeIcon icon="envelope" className="me-1" /> Email iD: tech@lxi.com
      </span>
    </div>

    <div className="mb-1">
      <a href="https://www.linkedin.com/company/lxisoft" target="_blank" rel="noreferrer" className="contact-link me-3" title="LinkedIn">
        <FontAwesomeIcon icon={['fab', 'linkedin']} size="2x" />
      </a>
      <a href="https://github.com/lxisoft" target="_blank" rel="noreferrer" className="contact-link" title="GitHub">
        <FontAwesomeIcon icon={['fab', 'github']} size="2x" />
      </a>
    </div>
  </div>
);

export default Footer;
