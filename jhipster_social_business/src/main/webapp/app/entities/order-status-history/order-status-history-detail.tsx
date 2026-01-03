import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './order-status-history.reducer';

export const OrderStatusHistoryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const orderStatusHistoryEntity = useAppSelector(state => state.orderStatusHistory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="orderStatusHistoryDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.orderStatusHistory.detail.title">OrderStatusHistory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{orderStatusHistoryEntity.id}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="whatsappProductServiceProApp.orderStatusHistory.status">Status</Translate>
            </span>
          </dt>
          <dd>{orderStatusHistoryEntity.status}</dd>
          <dt>
            <span id="changeTime">
              <Translate contentKey="whatsappProductServiceProApp.orderStatusHistory.changeTime">Change Time</Translate>
            </span>
          </dt>
          <dd>
            {orderStatusHistoryEntity.changeTime ? (
              <TextFormat value={orderStatusHistoryEntity.changeTime} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="onWayTime">
              <Translate contentKey="whatsappProductServiceProApp.orderStatusHistory.onWayTime">On Way Time</Translate>
            </span>
          </dt>
          <dd>
            {orderStatusHistoryEntity.onWayTime ? (
              <TextFormat value={orderStatusHistoryEntity.onWayTime} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="paymentPendingTime">
              <Translate contentKey="whatsappProductServiceProApp.orderStatusHistory.paymentPendingTime">Payment Pending Time</Translate>
            </span>
          </dt>
          <dd>
            {orderStatusHistoryEntity.paymentPendingTime ? (
              <TextFormat value={orderStatusHistoryEntity.paymentPendingTime} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/order-status-history" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/order-status-history/${orderStatusHistoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default OrderStatusHistoryDetail;
