import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './customer.reducer';

export const CustomerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const customerEntity = useAppSelector(state => state.customer.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="customerDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.customer.detail.title">Customer</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{customerEntity.id}</dd>
          <dt>
            <span id="waPhoneNumber">
              <Translate contentKey="whatsappProductServiceProApp.customer.waPhoneNumber">Wa Phone Number</Translate>
            </span>
          </dt>
          <dd>{customerEntity.waPhoneNumber}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="whatsappProductServiceProApp.customer.name">Name</Translate>
            </span>
          </dt>
          <dd>{customerEntity.name}</dd>
          <dt>
            <span id="phoneNumber">
              <Translate contentKey="whatsappProductServiceProApp.customer.phoneNumber">Phone Number</Translate>
            </span>
          </dt>
          <dd>{customerEntity.phoneNumber}</dd>
          <dt>
            <span id="locationLat">
              <Translate contentKey="whatsappProductServiceProApp.customer.locationLat">Location Lat</Translate>
            </span>
          </dt>
          <dd>{customerEntity.locationLat}</dd>
          <dt>
            <span id="locationLon">
              <Translate contentKey="whatsappProductServiceProApp.customer.locationLon">Location Lon</Translate>
            </span>
          </dt>
          <dd>{customerEntity.locationLon}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="whatsappProductServiceProApp.customer.address">Address</Translate>
            </span>
          </dt>
          <dd>{customerEntity.address}</dd>
          <dt>
            <span id="distanceFromBusinessKm">
              <Translate contentKey="whatsappProductServiceProApp.customer.distanceFromBusinessKm">Distance From Business Km</Translate>
            </span>
          </dt>
          <dd>{customerEntity.distanceFromBusinessKm}</dd>
          <dt>
            <span id="isPincodeValid">
              <Translate contentKey="whatsappProductServiceProApp.customer.isPincodeValid">Is Pincode Valid</Translate>
            </span>
          </dt>
          <dd>{customerEntity.isPincodeValid ? 'true' : 'false'}</dd>
          <dt>
            <span id="role">
              <Translate contentKey="whatsappProductServiceProApp.customer.role">Role</Translate>
            </span>
          </dt>
          <dd>{customerEntity.role}</dd>
          <dt>
            <span id="joinedAt">
              <Translate contentKey="whatsappProductServiceProApp.customer.joinedAt">Joined At</Translate>
            </span>
          </dt>
          <dd>{customerEntity.joinedAt ? <TextFormat value={customerEntity.joinedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="lastInteractionAt">
              <Translate contentKey="whatsappProductServiceProApp.customer.lastInteractionAt">Last Interaction At</Translate>
            </span>
          </dt>
          <dd>
            {customerEntity.lastInteractionAt ? (
              <TextFormat value={customerEntity.lastInteractionAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.customer.addedBy">Added By</Translate>
          </dt>
          <dd>{customerEntity.addedBy ? customerEntity.addedBy.name : ''}</dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.customer.zone">Zone</Translate>
          </dt>
          <dd>{customerEntity.zone ? customerEntity.zone.zoneName : ''}</dd>
        </dl>
        <Button tag={Link} to="/customer" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/customer/${customerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CustomerDetail;
