import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './removed-order-summary.reducer';

export const RemovedOrderSummaryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const removedOrderSummaryEntity = useAppSelector(state => state.removedOrderSummary.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="removedOrderSummaryDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.detail.title">RemovedOrderSummary</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{removedOrderSummaryEntity.id}</dd>
          <dt>
            <span id="userOriginalId">
              <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.userOriginalId">User Original Id</Translate>
            </span>
          </dt>
          <dd>{removedOrderSummaryEntity.userOriginalId}</dd>
          <dt>
            <span id="userName">
              <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.userName">User Name</Translate>
            </span>
          </dt>
          <dd>{removedOrderSummaryEntity.userName}</dd>
          <dt>
            <span id="userRole">
              <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.userRole">User Role</Translate>
            </span>
          </dt>
          <dd>{removedOrderSummaryEntity.userRole}</dd>
          <dt>
            <span id="totalOrders">
              <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.totalOrders">Total Orders</Translate>
            </span>
          </dt>
          <dd>{removedOrderSummaryEntity.totalOrders}</dd>
          <dt>
            <span id="totalAmount">
              <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.totalAmount">Total Amount</Translate>
            </span>
          </dt>
          <dd>{removedOrderSummaryEntity.totalAmount}</dd>
          <dt>
            <span id="firstInteractionAt">
              <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.firstInteractionAt">First Interaction At</Translate>
            </span>
          </dt>
          <dd>
            {removedOrderSummaryEntity.firstInteractionAt ? (
              <TextFormat value={removedOrderSummaryEntity.firstInteractionAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="lastInteractionAt">
              <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.lastInteractionAt">Last Interaction At</Translate>
            </span>
          </dt>
          <dd>
            {removedOrderSummaryEntity.lastInteractionAt ? (
              <TextFormat value={removedOrderSummaryEntity.lastInteractionAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="removedAt">
              <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.removedAt">Removed At</Translate>
            </span>
          </dt>
          <dd>
            {removedOrderSummaryEntity.removedAt ? (
              <TextFormat value={removedOrderSummaryEntity.removedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/removed-order-summary" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/removed-order-summary/${removedOrderSummaryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default RemovedOrderSummaryDetail;
