import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './button-action.reducer';

export const ButtonActionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const buttonActionEntity = useAppSelector(state => state.buttonAction.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="buttonActionDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.buttonAction.detail.title">ButtonAction</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{buttonActionEntity.id}</dd>
          <dt>
            <span id="waMessageId">
              <Translate contentKey="whatsappProductServiceProApp.buttonAction.waMessageId">Wa Message Id</Translate>
            </span>
          </dt>
          <dd>{buttonActionEntity.waMessageId}</dd>
          <dt>
            <span id="buttonId">
              <Translate contentKey="whatsappProductServiceProApp.buttonAction.buttonId">Button Id</Translate>
            </span>
          </dt>
          <dd>{buttonActionEntity.buttonId}</dd>
          <dt>
            <span id="clickedAt">
              <Translate contentKey="whatsappProductServiceProApp.buttonAction.clickedAt">Clicked At</Translate>
            </span>
          </dt>
          <dd>
            {buttonActionEntity.clickedAt ? <TextFormat value={buttonActionEntity.clickedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="clickedBy">
              <Translate contentKey="whatsappProductServiceProApp.buttonAction.clickedBy">Clicked By</Translate>
            </span>
          </dt>
          <dd>{buttonActionEntity.clickedBy}</dd>
        </dl>
        <Button tag={Link} to="/button-action" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/button-action/${buttonActionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ButtonActionDetail;
