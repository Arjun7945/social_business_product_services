import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './removed-user.reducer';

export const RemovedUserDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const removedUserEntity = useAppSelector(state => state.removedUser.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="removedUserDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.removedUser.detail.title">RemovedUser</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{removedUserEntity.id}</dd>
          <dt>
            <span id="originalId">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.originalId">Original Id</Translate>
            </span>
          </dt>
          <dd>{removedUserEntity.originalId}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.name">Name</Translate>
            </span>
          </dt>
          <dd>{removedUserEntity.name}</dd>
          <dt>
            <span id="role">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.role">Role</Translate>
            </span>
          </dt>
          <dd>{removedUserEntity.role}</dd>
          <dt>
            <span id="whatsappNumber">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.whatsappNumber">Whatsapp Number</Translate>
            </span>
          </dt>
          <dd>{removedUserEntity.whatsappNumber}</dd>
          <dt>
            <span id="phoneNumber">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.phoneNumber">Phone Number</Translate>
            </span>
          </dt>
          <dd>{removedUserEntity.phoneNumber}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.address">Address</Translate>
            </span>
          </dt>
          <dd>{removedUserEntity.address}</dd>
          <dt>
            <span id="locationLat">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.locationLat">Location Lat</Translate>
            </span>
          </dt>
          <dd>{removedUserEntity.locationLat}</dd>
          <dt>
            <span id="locationLon">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.locationLon">Location Lon</Translate>
            </span>
          </dt>
          <dd>{removedUserEntity.locationLon}</dd>
          <dt>
            <span id="joinedAt">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.joinedAt">Joined At</Translate>
            </span>
          </dt>
          <dd>
            {removedUserEntity.joinedAt ? <TextFormat value={removedUserEntity.joinedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="removedAt">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.removedAt">Removed At</Translate>
            </span>
          </dt>
          <dd>
            {removedUserEntity.removedAt ? <TextFormat value={removedUserEntity.removedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="reasonForRemoval">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.reasonForRemoval">Reason For Removal</Translate>
            </span>
          </dt>
          <dd>{removedUserEntity.reasonForRemoval}</dd>
          <dt>
            <span id="lastSessionData">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.lastSessionData">Last Session Data</Translate>
            </span>
          </dt>
          <dd>{removedUserEntity.lastSessionData}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.status">Status</Translate>
            </span>
          </dt>
          <dd>{removedUserEntity.status}</dd>
          <dt>
            <span id="orderHistoryId">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.orderHistoryId">Order History Id</Translate>
            </span>
          </dt>
          <dd>
            {removedUserEntity.orderHistoryId ? (
              <Link to={`/removed-order-summary/${removedUserEntity.orderHistoryId}`}>{removedUserEntity.orderHistoryId}</Link>
            ) : (
              ''
            )}
          </dd>
          <dt>
            <span id="distanceFromBusinessKm">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.distanceFromBusinessKm">Distance From Business Km</Translate>
            </span>
            <UncontrolledTooltip target="distanceFromBusinessKm">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.help.distanceFromBusinessKm" />
            </UncontrolledTooltip>
          </dt>
          <dd>{removedUserEntity.distanceFromBusinessKm}</dd>
          <dt>
            <span id="isPincodeValid">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.isPincodeValid">Is Pincode Valid</Translate>
            </span>
            <UncontrolledTooltip target="isPincodeValid">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.help.isPincodeValid" />
            </UncontrolledTooltip>
          </dt>
          <dd>{removedUserEntity.isPincodeValid ? 'true' : 'false'}</dd>
          <dt>
            <span id="zoneName">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.zoneName">Zone</Translate>
            </span>
          </dt>
          <dd>{removedUserEntity.zoneName}</dd>
        </dl>
        <Button tag={Link} to="/removed-user" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/removed-user/${removedUserEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default RemovedUserDetail;
