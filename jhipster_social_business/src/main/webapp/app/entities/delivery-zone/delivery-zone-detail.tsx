import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './delivery-zone.reducer';

export const DeliveryZoneDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const deliveryZoneEntity = useAppSelector(state => state.deliveryZone.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="deliveryZoneDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.deliveryZone.detail.title">DeliveryZone</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{deliveryZoneEntity.id}</dd>
          <dt>
            <span id="zoneName">
              <Translate contentKey="whatsappProductServiceProApp.deliveryZone.zoneName">Zone Name</Translate>
            </span>
            <UncontrolledTooltip target="zoneName">
              <Translate contentKey="whatsappProductServiceProApp.deliveryZone.help.zoneName" />
            </UncontrolledTooltip>
          </dt>
          <dd>{deliveryZoneEntity.zoneName}</dd>
          <dt>
            <span id="pincode">
              <Translate contentKey="whatsappProductServiceProApp.deliveryZone.pincode">Pincode</Translate>
            </span>
            <UncontrolledTooltip target="pincode">
              <Translate contentKey="whatsappProductServiceProApp.deliveryZone.help.pincode" />
            </UncontrolledTooltip>
          </dt>
          <dd>{deliveryZoneEntity.pincode}</dd>
        </dl>
        <Button tag={Link} to="/delivery-zone" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/delivery-zone/${deliveryZoneEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DeliveryZoneDetail;
