import './home.scss';

import React from 'react';
import { Link } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Alert, Col, Row } from 'reactstrap';

import { useAppSelector } from 'app/config/store';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
    <Row>
      <Col md="3" className="pad">
        <img src="content/images/footerlogo.jpg" alt="Product Services Logo" className="img-fluid rounded" />
      </Col>
      <Col md="9">
        <h1 className="display-4">
          <Translate contentKey="home.title">Welcome, Product Services!</Translate>
          <Translate contentKey="home.titleSuffix"> (Whatsapp Product Service)</Translate>
        </h1>
        <p className="lead">
          <Translate contentKey="home.subtitle">This is your product homepage</Translate>
        </p>
        {account?.login ? (
          <div>
            <Alert color="success">
              <Translate contentKey="home.logged.message" interpolate={{ username: account.login }}>
                You are logged in as user {account.login}.
              </Translate>
            </Alert>
          </div>
        ) : (
          <div>
            <Alert color="warning">
              <Translate contentKey="home.signin.prefix">If you want to </Translate>

              <Link to="/login" className="alert-link">
                <Translate contentKey="home.signin.link"> sign in</Translate>
              </Link>
              <Translate contentKey="home.signin.suffix">, use your registered account.</Translate>
            </Alert>

            <Alert color="warning">
              <Translate contentKey="global.messages.info.register.noaccount">You don&apos;t have an account yet?</Translate>&nbsp;
              <Link to="/account/register" className="alert-link">
                <Translate contentKey="global.messages.info.register.link">Register a new account</Translate>
              </Link>
            </Alert>
          </div>
        )}
        <p>
          <Translate contentKey="home.question">If you have any question contact dev:</Translate>
        </p>

        <ul>
          <li>
            <Link to="/contact-dev">
              <Translate contentKey="home.link.contact">Contact Dev</Translate>
            </Link>
          </li>
          <li>
            <a href="https://www.linkedin.com/company/lxisoft" target="_blank" rel="noopener noreferrer">
              <Translate contentKey="home.link.devsite">Visit Developer Site</Translate>
            </a>
          </li>
          <li>
            <Link to="/raise-ticket">
              <Translate contentKey="home.link.ticket">Create a Issue Ticket</Translate>
            </Link>
          </li>
        </ul>

        <p>
          <Translate contentKey="home.like">If you like our Work, don&apos;t forget to give us a star on</Translate>{' '}
          <a href="https://github.com/lxisoft" target="_blank" rel="noopener noreferrer">
            <Translate contentKey="home.github">GitHub</Translate>
          </a>
          !
        </p>
      </Col>
    </Row>
  );
};

export default Home;
