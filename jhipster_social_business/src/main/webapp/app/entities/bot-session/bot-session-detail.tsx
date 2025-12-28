import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './bot-session.reducer';

export const BotSessionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const botSessionEntity = useAppSelector(state => state.botSession.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="botSessionDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.botSession.detail.title">BotSession</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{botSessionEntity.id}</dd>
          <dt>
            <span id="waPhoneNumber">
              <Translate contentKey="whatsappProductServiceProApp.botSession.waPhoneNumber">Wa Phone Number</Translate>
            </span>
          </dt>
          <dd>{botSessionEntity.waPhoneNumber}</dd>
          <dt>
            <span id="currentState">
              <Translate contentKey="whatsappProductServiceProApp.botSession.currentState">Current State</Translate>
            </span>
          </dt>
          <dd>{botSessionEntity.currentState}</dd>
          <dt>
            <span id="sessionData">
              <Translate contentKey="whatsappProductServiceProApp.botSession.sessionData">Session Data</Translate>
            </span>
          </dt>
          <dd>{botSessionEntity.sessionData}</dd>
          <dt>
            <span id="lastActiveAt">
              <Translate contentKey="whatsappProductServiceProApp.botSession.lastActiveAt">Last Active At</Translate>
            </span>
          </dt>
          <dd>
            {botSessionEntity.lastActiveAt ? (
              <TextFormat value={botSessionEntity.lastActiveAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/bot-session" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/bot-session/${botSessionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BotSessionDetail;
