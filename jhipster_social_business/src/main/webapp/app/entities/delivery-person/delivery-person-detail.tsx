import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './delivery-person.reducer';

export const DeliveryPersonDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const deliveryPersonEntity = useAppSelector(state => state.deliveryPerson.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="deliveryPersonDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.detail.title">DeliveryPerson</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{deliveryPersonEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.name">Name</Translate>
            </span>
          </dt>
          <dd>{deliveryPersonEntity.name}</dd>
          <dt>
            <span id="waPhoneNumber">
              <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.waPhoneNumber">Wa Phone Number</Translate>
            </span>
          </dt>
          <dd>{deliveryPersonEntity.waPhoneNumber}</dd>
          <dt>
            <span id="phoneNumber">
              <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.phoneNumber">Phone Number</Translate>
            </span>
          </dt>
          <dd>{deliveryPersonEntity.phoneNumber}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.status">Status</Translate>
            </span>
            <UncontrolledTooltip target="status">
              <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.help.status" />
            </UncontrolledTooltip>
          </dt>
          <dd>{deliveryPersonEntity.status}</dd>
          <dt>
            <span id="joinedAt">
              <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.joinedAt">Joined At</Translate>
            </span>
          </dt>
          <dd>
            {deliveryPersonEntity.joinedAt ? (
              <TextFormat value={deliveryPersonEntity.joinedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{deliveryPersonEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.addedBy">Added By</Translate>
          </dt>
          <dd>
            {deliveryPersonEntity.addedBy ? (
              <Link to={`/team-member/${deliveryPersonEntity.addedBy.id}`}>{deliveryPersonEntity.addedBy.name}</Link>
            ) : (
              ''
            )}
          </dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.zone">Zone</Translate>
          </dt>
          <dd>
            {deliveryPersonEntity.zone ? (
              <Link to={`/delivery-zone/${deliveryPersonEntity.zone.id}`}>{deliveryPersonEntity.zone.zoneName}</Link>
            ) : (
              ''
            )}
          </dd>
        </dl>
        <Button tag={Link} to="/delivery-person" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/delivery-person/${deliveryPersonEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DeliveryPersonDetail;
