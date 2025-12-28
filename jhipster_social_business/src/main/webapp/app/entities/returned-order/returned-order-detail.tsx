import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './returned-order.reducer';

export const ReturnedOrderDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const returnedOrderEntity = useAppSelector(state => state.returnedOrder.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="returnedOrderDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.returnedOrder.detail.title">ReturnedOrder</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{returnedOrderEntity.id}</dd>
          <dt>
            <span id="returnDate">
              <Translate contentKey="whatsappProductServiceProApp.returnedOrder.returnDate">Return Date</Translate>
            </span>
          </dt>
          <dd>
            {returnedOrderEntity.returnDate ? (
              <TextFormat value={returnedOrderEntity.returnDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="paymentReceivedMode">
              <Translate contentKey="whatsappProductServiceProApp.returnedOrder.paymentReceivedMode">Payment Received Mode</Translate>
            </span>
          </dt>
          <dd>{returnedOrderEntity.paymentReceivedMode}</dd>
          <dt>
            <span id="paymentReturnedMode">
              <Translate contentKey="whatsappProductServiceProApp.returnedOrder.paymentReturnedMode">Payment Returned Mode</Translate>
            </span>
          </dt>
          <dd>{returnedOrderEntity.paymentReturnedMode}</dd>
          <dt>
            <span id="productClaimStatus">
              <Translate contentKey="whatsappProductServiceProApp.returnedOrder.productClaimStatus">Product Claim Status</Translate>
            </span>
          </dt>
          <dd>{returnedOrderEntity.productClaimStatus}</dd>
          <dt>
            <span id="refundAmount">
              <Translate contentKey="whatsappProductServiceProApp.returnedOrder.refundAmount">Refund Amount</Translate>
            </span>
          </dt>
          <dd>{returnedOrderEntity.refundAmount}</dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.returnedOrder.order">Order</Translate>
          </dt>
          <dd>{returnedOrderEntity.order ? returnedOrderEntity.order.id : ''}</dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.returnedOrder.customer">Customer</Translate>
          </dt>
          <dd>{returnedOrderEntity.customer ? returnedOrderEntity.customer.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/returned-order" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/returned-order/${returnedOrderEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ReturnedOrderDetail;
